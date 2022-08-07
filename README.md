## 设计文档及规范
基于Apache Calcite实现DSL统一查询服务。
Apache Calcite是一个动态数据管理框架。 它包含许多组成典型数据库管理系统的部分，但省略了存储原语。
它提供了行业标准的SQL解析器和验证器，具有可插入规则和成本函数的可自定义优化器，逻辑和物理代数运算符，从SQL到代数的各种转换。

```
dsl-client.jar(应用程序)--jdbc-->dsl-server.jar(DSL查询服务)-->mysql,mongodb
```

dsl-client提供两个接口验证dsl接口和原生mysql接口,参数sql需要urlencode
- http://127.0.0.1:8080/jdbc/avatica?sql=
- http://127.0.0.1:8080/jdbc/mysql?sql=

## 环境准备
$ docker run -d --name mongo -p 27017:27017 mongo:4.4.12

$ docker run -d --name=mysql -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 mysql:5.7

## 数据准备
- orders.txt导入mysql(db-mysql.sql)
- customer.txt导入mysql(db-mysql.sql)
- nation.txt导入mongodb(db-mongo.sql)

## 程序
$ mvn clean package -Dmaven.test.skip=true
$ java -jar dsl-client.jar --spring.config.location=./application.properties
$ java -jar dsl-server-jar-with-dependencies.jar -m ./dsl-model.json -s ./mongo-schema.json

### 功能测试
```
Select * from db_mysql.ORDERS where O_ORDERKEY = 2 AND O_ORDERDATE >= '1996-12-01' AND O_ORDERDATE <= '1996-12-01'
```
http://127.0.0.1:8080/jdbc/avatica?sql=Select%20*%20from%20db_mysql.ORDERS%20where%20O_ORDERKEY%20%3D%202%20AND%20O_ORDERDATE%20%3E%3D%20'1996-12-01'%20AND%20O_ORDERDATE%20%3C%3D%20'1996-12-01'

```
Select O_ORDERKEY,count(1) as cnt 
from db_mysql.ORDERS 
group by O_ORDERKEY 
having count(1) > 0 
order by cnt limit 10
```
http://127.0.0.1:8080/jdbc/avatica?sql=Select%20O_ORDERKEY,count(1)%20as%20cnt%20from%20db_mysql.ORDERS%20group%20by%20O_ORDERKEY%20having%20count(1)%20%3E%200%20order%20by%20cnt%20limit%2010

```
Select * 
from db_mysql.ORDERS o,db_mysql.CUSTOMER c,db_mongo.NATION n 
where o.O_CUSTKEY = c.C_CUSTKEY and c.C_NATIONKEY = n.N_NATIONKEY and c.C_NAME = 'Customer#000000002'
```
http://127.0.0.1:8080/jdbc/avatica?sql=Select%20*%20from%20db_mysql.ORDERS%20o,db_mysql.CUSTOMER%20c,db_mongo.NATION%20n%20where%20o.O_CUSTKEY%20%3D%20c.C_CUSTKEY%20and%20c.C_NATIONKEY%20%3D%20n.N_NATIONKEY%20and%20c.C_NAME%20%3D%20'Customer%23000000002'

```
Select C_NAME,Count(O_ORDERKEY) as cnt 
From ( 
Select o.O_ORDERKEY,c.C_CUSTKEY,c.C_NAME 
from db_mysql.ORDERS o 
Left join db_mysql.CUSTOMER c 
On o.O_CUSTKEY = c.C_CUSTKEY )t 
Group by C_NAME 
Having Count(O_ORDERKEY) > 0 
order by cnt desc 
limit 10
```
http://127.0.0.1:8080/jdbc/avatica?sql=Select%20C_NAME,Count(O_ORDERKEY)%20as%20cnt%20From%20(%20Select%20o.O_ORDERKEY,c.C_CUSTKEY,c.C_NAME%20from%20db_mysql.ORDERS%20o%20Left%20join%20db_mysql.CUSTOMER%20c%20On%20o.O_CUSTKEY%20%3D%20c.C_CUSTKEY%20)t%20Group%20by%20C_NAME%20Having%20Count(O_ORDERKEY)%20%3E%200%20order%20by%20cnt%20desc%20limit%2010

### 性能测试
$ ab -n 1000 -c 100 http://127.0.0.1:8080/jdbc/avatica?sql=select%20*%20from%20ORDERS%20limit%2010

$ ab -n 1000 -c 100 http://127.0.0.1:8080/jdbc/mysql?sql=select%20*%20from%20ORDERS%20limit%2010