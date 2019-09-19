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
/* eslint-disable import/no-unresolved */
import { connect } from 'react-redux';
import { utils } from 'react-eco-ui';
import { getSchemasAsync } from '../../../../actions/schemasActions/schemasActions';
import { closeSchema } from '../../../../actions/schemaActions/schemaActions';
import {
  getLimitedTotalPages,
  getLimitedTotalElements,
  getPageSize,
} from '../../../../selectors/schemasSelectors/schemasSelectors';
import Paginator from './Paginator';

const { functional } = utils;
const { noop } = functional;

const mapStateToProps = state => ({
  page: state.schemasReducer.page + 1,
  number: state.schemasReducer.page,
  totalPages: getLimitedTotalPages(state),
  totalElements: getLimitedTotalElements(state),
  size: getPageSize(state),
  first: state.schemasReducer.first,
  last: state.schemasReducer.last,
});

const mapDispatchToProps = dispatch => ({
  onChangePage: (page) => {
    dispatch(getSchemasAsync({ page: page - 1 }));
    dispatch(closeSchema());
  },
  onChangeSize: (size) => {
    dispatch(getSchemasAsync({ pageSize: size, page: 0 }));
    dispatch(closeSchema());
  },
});

const mergeProps = (stateProps, dispatchProps, ownProps) => (
  Object.assign({}, stateProps, ownProps, dispatchProps, {
    onPage: page => (stateProps.page !== page ? dispatchProps.onChangePage(page) : noop()),
    onSize: size => (stateProps.size !== size ? dispatchProps.onChangeSize(size) : noop()),
  })
);

export default connect(
  mapStateToProps,
  mapDispatchToProps,
  mergeProps,
)(Paginator);
