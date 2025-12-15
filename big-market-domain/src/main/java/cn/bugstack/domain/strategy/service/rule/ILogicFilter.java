package cn.bugstack.domain.strategy.service.rule;

import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;

/**
 * @program: big-market
 * @description: 抽奖规则过滤接口
 * @author: hxy
 * @create: 2025-12-15 22:34
 **/

public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity>{
    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);
}
