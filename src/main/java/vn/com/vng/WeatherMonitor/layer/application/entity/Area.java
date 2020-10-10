package vn.com.vng.WeatherMonitor.layer.application.entity;

import java.util.List;

public class Area {
    private Integer id;
    private String name;
    private String code;
    private Integer parentID;
    private List<Area> subAreas;

    public List<Area> getSubAreas() {
        return subAreas;
    }

    public void setSubAreas(List<Area> subAreas) {
        this.subAreas = subAreas;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }
}
