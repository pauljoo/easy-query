https://blog.51cto.com/xpleaf/2639844

## Hive 和 Flink
Hive 自己做了SQL解析，使用了Calcite的查询优化功能。
Flink 从解析到优化都直接使用了Calcite。

## 基本概念
关系代数(Relational Algebra): RelNode。例如Sort, Join, Project, Filter, Scan, Sample。
行表达式(Row expressions):
特征(Trait)

###
$ docker run -d --name=mysql -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 mysql:5.7
