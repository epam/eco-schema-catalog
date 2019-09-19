/*
 * Copyright 2019 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.epam.eco.schemacatalog.fts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Raman_Babich
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public final class SearchResult<T> implements Iterable<T> {

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final long maxResultWindow;
    private final Map<String, Map<String, Long>> aggregations;

    public SearchResult(
            Page<T> page,
            Map<String, Map<String, Long>> aggregations) {
        this(page, Long.MAX_VALUE, aggregations);
    }

    public SearchResult(
            Page<T> page,
            long maxResultWindow,
            Map<String, Map<String, Long>> aggregations) {
        this(
                Validate.notNull(page, "Page can't be null").getContent(),
                Validate.notNull(page, "Page can't be null").getNumber(),
                Validate.notNull(page, "Page can't be null").getSize(),
                Validate.notNull(page, "Page can't be null").getTotalElements(),
                maxResultWindow,
                aggregations);
    }

    public SearchResult(
            List<T> content,
            int pageNumber,
            int pageSize,
            long totalElements,
            Map<String, Map<String, Long>> aggregations) {
        this(
                content,
                pageNumber,
                pageSize,
                totalElements,
                Long.MAX_VALUE,
                aggregations);
    }

    @JsonCreator
    public SearchResult(
            @JsonProperty("content") List<T> content,
            @JsonProperty("pageNumber") int pageNumber,
            @JsonProperty("pageSize") int pageSize,
            @JsonProperty("totalElements") long totalElements,
            @JsonProperty("maxResultWindow") long maxResultWindow,
            @JsonProperty("aggregations") Map<String, Map<String, Long>> aggregations) {
        Validate.notNull(content, "Content is null");
        Validate.isTrue(pageNumber >= 0, "Page Number is invalid");
        Validate.isTrue(pageSize > 0, "Page Size is invalid");
        Validate.isTrue(totalElements >= 0, "Number of Total Elements is invalid");
        Validate.isTrue(maxResultWindow > 0, "Max Result Window is invalid");
        Validate.isTrue(
                pageNumber * pageSize + content.size() <= maxResultWindow,
                "Result window is too large");

        this.content = Collections.unmodifiableList(new ArrayList<>(content));
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.maxResultWindow = maxResultWindow;
        this.aggregations =
                aggregations != null ?
                aggregations.entrySet().stream().
                collect(
                        Collectors.collectingAndThen(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        e -> Collections.unmodifiableMap(new HashMap<>(e.getValue()))),
                                Collections::unmodifiableMap)) :
                Collections.emptyMap();
    }

    public List<T> getContent() {
        return content;
    }
    public int getPageNumber() {
        return pageNumber;
    }
    public int getPageSize() {
        return pageSize;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public long getMaxResultWindow() {
        return maxResultWindow;
    }
    public Map<String, Map<String, Long>> getAggregations() {
        return aggregations;
    }

    public long getTotalPages() {
        return (int) Math.ceil((double)Math.min(totalElements, maxResultWindow) / (double) pageSize);
    }

    public boolean isLastPage() {
        return !isHasNextPage();
    }

    public boolean isFirstPage() {
        return !isHasPreviousPage();
    }

    public boolean isHasPreviousPage() {
        return pageNumber > 0;
    }

    public boolean isHasNextPage() {
        return pageNumber + 1 < getTotalPages();
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

    public Map<String, Long> getAggregation(String name) {
        return aggregations.get(name);
    }

    public <S> SearchResult<S> map(Function<T, S> mapper) {
        Validate.notNull(mapper, "Mapper is null");

        return new SearchResult<>(
                content.stream().map(mapper).collect(Collectors.toList()),
                pageNumber,
                pageSize,
                totalElements,
                maxResultWindow,
                aggregations);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SearchResult<?> that = (SearchResult<?>) obj;
        return
                Objects.equals(this.content, that.content) &&
                Objects.equals(this.pageNumber, that.pageNumber) &&
                Objects.equals(this.pageSize, that.pageSize) &&
                Objects.equals(this.totalElements, that.totalElements) &&
                Objects.equals(this.maxResultWindow, that.maxResultWindow) &&
                Objects.equals(this.aggregations, that.aggregations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                content,
                pageNumber,
                pageSize,
                totalElements,
                maxResultWindow,
                aggregations);
    }

    @Override
    public String toString() {
        return
                "{content: " + content +
                ", pageNumber: " + pageNumber +
                ", pageSize: " + pageSize +
                ", totalElements: " + totalElements +
                ", maxResultWindow: " + maxResultWindow +
                ", aggregations: " + aggregations +
                "}";
    }

}
