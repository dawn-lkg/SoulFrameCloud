package com.clm.common.core.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户昵称生成器
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
public class NicknameGenerator {

    /**
     * 姓氏
     */
    private static final String[] SURNAMES = {
            "李", "王", "张", "刘", "陈", "杨", "赵", "黄", "周", "吴",
            "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗",
            "梁", "宋", "郑", "谢", "韩", "唐", "冯", "于", "董", "萧"
    };

    /**
     * 名字常用字
     */
    private static final String[] NAME_CHARS = {
            "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军",
            "洋", "勇", "艳", "杰", "娟", "涛", "明", "超", "秀兰", "霞",
            "平", "刚", "桂英", "博", "慧", "燕", "振", "莉", "凯", "彬"
    };

    /**
     * 动物名称
     */
    private static final String[] ANIMALS = {
            "熊猫", "老虎", "狮子", "猴子", "大象", "长颈鹿", "狐狸", "兔子", "浣熊", "猫头鹰",
            "鹦鹉", "蝴蝶", "蜜蜂", "小猫", "小狗", "刺猬", "海豚", "北极熊", "考拉", "斑马"
    };

    /**
     * 形容词
     */
    private static final String[] ADJECTIVES = {
            "快乐", "勇敢", "聪明", "可爱", "活泼", "开朗", "机智", "温柔", "善良", "幽默",
            "阳光", "文静", "调皮", "睿智", "美丽", "帅气", "可靠", "真诚", "热情", "淘气"
    };

    /**
     * 颜色
     */
    private static final String[] COLORS = {
            "红", "橙", "黄", "绿", "蓝", "紫", "粉", "青", "银", "金"
    };

    /**
     * 随机生成中文姓名
     *
     * @return 随机姓名
     */
    public static String generateChineseName() {
        Random random = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder();
        
        // 随机选择姓氏
        sb.append(SURNAMES[random.nextInt(SURNAMES.length)]);
        
        // 随机选择名字(1-2个字)
        int nameLength = random.nextInt(2) + 1;
        for (int i = 0; i < nameLength; i++) {
            sb.append(NAME_CHARS[random.nextInt(NAME_CHARS.length)]);
        }
        
        return sb.toString();
    }

    /**
     * 生成可爱风格的昵称(形容词+动物)
     *
     * @return 可爱风格昵称
     */
    public static String generateCuteName() {
        Random random = ThreadLocalRandom.current();
        return ADJECTIVES[random.nextInt(ADJECTIVES.length)] + 
               ANIMALS[random.nextInt(ANIMALS.length)];
    }

    /**
     * 生成颜色风格的昵称(颜色+动物/名字)
     *
     * @return 颜色风格昵称
     */
    public static String generateColorName() {
        Random random = ThreadLocalRandom.current();
        if (random.nextBoolean()) {
            return COLORS[random.nextInt(COLORS.length)] + 
                   ANIMALS[random.nextInt(ANIMALS.length)];
        } else {
            return COLORS[random.nextInt(COLORS.length)] + 
                   NAME_CHARS[random.nextInt(NAME_CHARS.length)];
        }
    }

    /**
     * 生成用户昵称(随机选择生成方式)
     *
     * @return 随机生成的用户昵称
     */
    public static String generateNickname() {
        Random random = ThreadLocalRandom.current();
        int type = random.nextInt(3);
        
        switch (type) {
            case 0:
                return generateChineseName();
            case 1:
                return generateCuteName();
            case 2:
                return generateColorName();
            default:
                return generateChineseName();
        }
    }

    /**
     * 生成带数字后缀的用户昵称
     *
     * @return 带数字后缀的用户昵称
     */
    public static String generateNicknameWithNumber() {
        Random random = ThreadLocalRandom.current();
        String nickname = generateNickname();
        
        // 50%的概率添加数字后缀
        if (random.nextBoolean()) {
            int suffix = random.nextInt(9000) + 1000; // 1000-9999之间的随机数
            nickname = nickname + suffix;
        }
        
        return nickname;
    }
} 