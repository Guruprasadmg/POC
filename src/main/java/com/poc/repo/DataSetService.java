package com.poc.repo;

import com.poc.entity.DataSet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataSetService extends CrudRepository<DataSet, String> {

    List<DataSet> getDataSetByStock(String stock);
}