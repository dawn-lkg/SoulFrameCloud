package com.clm.common.core.domain.server;

import com.clm.common.core.utils.Arith;
import lombok.Data;

/**
 * @author chenliming
 * @date 2023/11/16 23:00
 */
@Data
public class Cpu {
    /**
     * 核心数
     */
    private int cpuNum;
    /**
     * cpy总使用率
     */
    private double total;
    /**
     * cpu系统的使用率
     */
    private double sys;
    /**
     * cpu用户使用率
     */
    private double used;
    /**
     * cpu当前等待率
     */
    private double wait;
    /**
     * cpu当前空闲率
     */
    private double free;
    /**
     * cpu型号
     */
    private String cpuModel;
    /**
     * 线程数
     */
    private int threadNum;

    /**
     * cpu位数
     */
    private int cpuArch;


    public double getTotal() {
        return Arith.round(Arith.mul(total, 100), 2);
    }

    public double getSys() {
        return Arith.round(Arith.mul(sys / total, 100), 2);
    }

    public double getUsed() {
        return Arith.round(Arith.mul(used / total, 100), 2);
    }

    public double getWait() {
        return Arith.round(Arith.mul(wait / total, 100), 2);
    }

    public double getFree() {
        return Arith.round(Arith.mul(free / total, 100), 2);
    }
}

