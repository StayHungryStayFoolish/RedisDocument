package io.stayhungrystayfoolish.redis.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/4/28 5:40 下午
 * @Description:
 * @Version: 1.0
 */
public class User implements Serializable {

    private String name;
    private Integer age;
    private List<String> grades;
    private Instant marryDate;

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

    public List<String> getGrades() {
        return grades;
    }

    public void setGrades(List<String> grades) {
        this.grades = grades;
    }

    public Instant getMarryDate() {
        return marryDate;
    }

    public void setMarryDate(Instant marryDate) {
        this.marryDate = marryDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", grades=" + grades +
                ", marryDate=" + marryDate +
                '}';
    }
}
