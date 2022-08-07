package info.xpanda.dsl.client.controller;

import info.xpanda.dsl.client.DbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Paul Jiang
 */
@RestController
@RequestMapping("/jdbc")
public class JdbcContorller {
    private static final Logger log = LoggerFactory.getLogger(JdbcContorller.class);

    @Resource(name = "avaticaDbUtil")
    private DbUtil avaticaDbUtil;

    @Resource(name = "mysqlDbUtil")
    private DbUtil mysqlDbUtil;

    @RequestMapping("/avatica")
    @ResponseBody
    public List<Map<String, Object>> avatica(String sql) throws Exception {
        log.info(sql);
        return avaticaDbUtil.select(sql);
    }

    @RequestMapping("/mysql")
    @ResponseBody
    public List<Map<String, Object>> mysql(String sql) throws Exception {
        return mysqlDbUtil.select(sql);
    }
}
