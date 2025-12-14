package cn.bugstack.test.domain;

import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @program: big-market
 * @description:
 * @author: hxy
 * @create: 2025-12-13 16:00
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmoryDispatchTest {


    @Resource
    private IStrategyDispatch strategyDispatch;

    @Resource
    private IStrategyArmory strategyArmory;

    @Before
    public void test_strategyArmory() {
        boolean success=strategyArmory.assembleLottryStrategy(100001L);
        log.info("测试结果{}",success);
    }

    @Test
    public void test_getAssembleRandomVal() {
        log.info("测试结果：{} - 奖品ID值", strategyDispatch.getRandomAwaradId(100001L));
    }

    @Test
    public void test_getAssembleRandomVal_ruleWeight() {
        //log.info("测试结果：{} - 4000幸运值", strategyDispatch.getRandomAwardId(100001L,"4000:102,103,104,105"));
        //log.info("测试结果：{} - 5000幸运值", strategyDispatch.getRandomAwardId(100001L,"5000:102,103,104,105,106,107"));
        log.info("测试结果：{} - 6000幸运值", strategyDispatch.getRandomAwardId(100001L,"6000:102,103,104,105,106,107,108,109"));
    }

    @Resource
    private IRedisService redisService;

    @Test
    public void test_map() {
        RMap<Integer, Integer> map = redisService.getMap("strategy_id_100001");
        map.put(1, 101);
        map.put(2, 101);
        map.put(3, 101);
        map.put(4, 102);
        map.put(5, 102);
        map.put(6, 102);
        map.put(7, 103);
        map.put(8, 103);
        map.put(9, 104);
        map.put(10, 105);

        log.info("测试结果：{}", redisService.getMap("strategy_id_100001").get(1));
    }
}
