package com.chen.chenaiagent.agent;

import com.chen.chenaiagent.exception.BusinessException;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 模型的抽象代理类
 * 实现思考到循环的模式
 * @author chen
 * @date 2023/10/23
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ReActAgent extends BaseAgent{
    public abstract boolean think();
    public abstract String act();

    /**
     * 单步执行
     * @return
     */
    @Override
    public String step(){
        try {
            boolean shouldAct = think();
            if (!shouldAct){
                return "思考完成 - 无需行动";
            }
            return act();

        }catch (BusinessException e){
            e.printStackTrace();
            return "步骤执行失败：" + e.getMessage();
        }
    }
}
