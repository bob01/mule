/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring.parsers.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleCollectionObject
{

    private Map<String, List<String>> simpleListTypeMap;
    private List<String> simpleTypeList;
    private Map<String, Integer> simpleTypeMap;
    private Map<Long, SimpleCollectionObject> complexTypeMap;
    private List<String> simpleTypeListWithConverter;
    private Set<String> simpleTypeSet;
    private Map<Object, Object> simpleParameters = new HashMap<>();

    public void setSimpleParameters(Map<Object, Object> simpleParameters)
    {
        this.simpleParameters = simpleParameters;
    }

    public Map<Object, Object> getSimpleParameters()
    {
        return simpleParameters;
    }

    public List<String> getSimpleTypeList()
    {
        return simpleTypeList;
    }

    public void setSimpleTypeList(List<String> simpleTypeList)
    {
        this.simpleTypeList = simpleTypeList;
    }

    public Set<String> getSimpleTypeSet()
    {
        return simpleTypeSet;
    }

    public void setSimpleTypeSet(Set<String> simpleTypeSet)
    {
        this.simpleTypeSet = simpleTypeSet;
    }

    public List<String> getSimpleTypeListWithConverter()
    {
        return simpleTypeListWithConverter;
    }

    public void setSimpleTypeListWithConverter(List<String> simpleTypeListWithConverter)
    {
        this.simpleTypeListWithConverter = simpleTypeListWithConverter;
    }

    public Map<String, Integer> getSimpleTypeMap()
    {
        return simpleTypeMap;
    }

    public void setSimpleTypeMap(Map<String, Integer> simpleTypeMap)
    {
        this.simpleTypeMap = simpleTypeMap;
    }

    public Map<Long, SimpleCollectionObject> getComplexTypeMap()
    {
        return complexTypeMap;
    }

    public void setComplexTypeMap(Map<Long, SimpleCollectionObject> complexTypeMap)
    {
        this.complexTypeMap = complexTypeMap;
    }

    public Map<String, List<String>> getSimpleListTypeMap()
    {
        return simpleListTypeMap;
    }

    public void setSimpleListTypeMap(Map<String, List<String>> simpleListTypeMap)
    {
        this.simpleListTypeMap = simpleListTypeMap;
    }
}
