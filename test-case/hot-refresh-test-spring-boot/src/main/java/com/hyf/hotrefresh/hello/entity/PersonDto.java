package com.hyf.hotrefresh.hello.entity;

import java.util.Date;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class PersonDto {

    private Long    id;
    private String  name;
    private Integer age;
    private Date    birthday;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
