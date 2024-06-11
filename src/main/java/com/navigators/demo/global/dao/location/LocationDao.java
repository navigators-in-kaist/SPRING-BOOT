package com.navigators.demo.global.dao.location;

import com.navigators.demo.global.entity.Location;

import java.util.List;

public interface LocationDao {

    List<Location> getLocationListByCategoryId(String categoryId);

}
