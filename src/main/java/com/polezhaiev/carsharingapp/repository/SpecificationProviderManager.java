package com.polezhaiev.carsharingapp.repository;

public interface SpecificationProviderManager<T> {
    SpecificationProvider<T> getSpecification(Object key);
}
