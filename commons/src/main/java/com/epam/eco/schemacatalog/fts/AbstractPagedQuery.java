/*
 * Copyright 2020 EPAM Systems
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

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * @author Andrei_Tytsik
 */
public abstract class AbstractPagedQuery {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;

    private int page;
    private int pageSize;

    public AbstractPagedQuery() {
        this(DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    public AbstractPagedQuery(Pageable pageable) {
        this(
                pageable != null ? pageable.getPageNumber() : DEFAULT_PAGE,
                pageable != null ? pageable.getPageSize() : DEFAULT_PAGE_SIZE);
    }

    public AbstractPagedQuery(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
    public int getPage() {
        return page;
    }
    public int getPageOrDefaultIfInvalid() {
        return page >= 0 ? page : DEFAULT_PAGE;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getPageSize() {
        return pageSize;
    }
    public int getPageSizeOrDefaultIfInvalid() {
        return pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public Pageable getPageable() {
        return PageRequest.of(getPageOrDefaultIfInvalid(), getPageSizeOrDefaultIfInvalid());
    }

}
