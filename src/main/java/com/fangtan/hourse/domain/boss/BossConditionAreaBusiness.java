package com.fangtan.hourse.domain.boss;

import lombok.Data;

import java.util.List;

@Data
public class BossConditionAreaBusiness extends BossBaseCondition {

    private List<BossBaseCondition> children;
}
