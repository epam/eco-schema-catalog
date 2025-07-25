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
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import transformTableBody from './utils/transformTableBody/transformTableBody';
import IconButton from '../IconButton/IconButton';
import closeIcon from '../../assets/close.svg';
import expandIcon from '../../assets/expand.svg';
import collapseIcon from '../../assets/collapse.svg';

import './TableWithDetails.scss';

class TableWithDetails extends Component {
  static propTypes = {
    children: PropTypes.node,
    body: PropTypes.array.isRequired,
    headers: PropTypes.array,
    selectedRowIndex: PropTypes.number,
    hoverableRow: PropTypes.bool,
    rowHeight: PropTypes.number,
    isStripes: PropTypes.bool,
    expandHeight: PropTypes.number,
    onRowClick: PropTypes.func,
    onCloseDetails: PropTypes.func,
    onExpandeDetails: PropTypes.func,
  }

  static defaultProps = {
    rowHeight: 36,
    isStripes: false,
    expandHeight: 0,
  }

  constructor(props) {
    super(props);
    this.state = {
      numberOfRows: 10,
      selectedRowIndex: props.selectedRowIndex,
      isExpanded: false,
      columnWidths: props.headers ? props.headers.map(() => null) : [],
      resizing: null,
    };
    this.tableRef = React.createRef();
  }

  componentDidUpdate(prevProps) {
    if (prevProps.headers !== this.props.headers) {
      this.setState({
        columnWidths: this.props.headers.map((header) => header.weight),
      });
    }
  }

  handleResizeStart = (e, colIndex) => {
    e.preventDefault();
    e.stopPropagation();
    const startX = e.clientX;
    const startWidth = this.getColumnWidthPx(colIndex);
    this.setState({
      resizing: { colIndex, startX, startWidth },
    });
    document.addEventListener('mousemove', this.handleResizing);
    document.addEventListener('mouseup', this.handleResizeEnd);
  }

  handleResizing = (e) => {
    const { resizing, columnWidths } = this.state;
    if (!resizing) return;
    const { colIndex, startX, startWidth } = resizing;
    const delta = e.clientX - startX;
    const newWidth = Math.max(50, startWidth + delta); // min width 50px
    const newColumnWidths = [...columnWidths];
    newColumnWidths[colIndex] = newWidth;
    this.setState({ columnWidths: newColumnWidths });
  }

  handleResizeEnd = () => {
    this.setState({ resizing: null });
    document.removeEventListener('mousemove', this.handleResizing);
    document.removeEventListener('mouseup', this.handleResizeEnd);
  }

  getColumnWidthPx = (colIndex) => {
    // Try to get actual px width from DOM, fallback to 120px
    if (this.tableRef.current) {
      const headerCells = this.tableRef.current.querySelectorAll('.row.header .cell');
      if (headerCells[colIndex]) {
        return headerCells[colIndex].offsetWidth;
      }
    }
    return 120;
  }

  handleRowClick = (e, rowIndex) => {
    const { onRowClick } = this.props;
    if (onRowClick) {
      onRowClick(rowIndex);
      this.setState({ selectedRowIndex: rowIndex });
    }
  }

  handleRowKeyDown = () => {
    this.keyDownTime = new Date().getTime();
  }

  handleRowKeyUp = (e, rowIndex) => {
    const time = new Date().getTime();
    if ((time - this.keyDownTime) < 500) {
      this.handleRowClick(e, rowIndex);
    }
  }

  handleCloseDetails = () => {
    const { onCloseDetails } = this.props;
    onCloseDetails();
    this.setState({ selectedRowIndex: null, isExpanded: false });
  }

  handleExpand = () => {
    const { onExpandeDetails } = this.props;
    if (onExpandeDetails) {
      onExpandeDetails();
    } else {
      const { isExpanded } = this.state;
      this.setState({ isExpanded: !isExpanded });
    }
  }

  render() {
    const {
      children,
      body,
      headers,
      hoverableRow,
      rowHeight,
      isStripes,
      expandHeight,
    } = this.props;
    const {
      numberOfRows,
      selectedRowIndex,
      isExpanded,
      columnWidths,
    } = this.state;
    const tableHeight = (numberOfRows + 1) * rowHeight;
    const isHaveDetails = !!React.Children.count(children);
    const tableData = transformTableBody(headers, body);
    const firstColumnWeight = tableData[0] ? tableData[0].weights[0] : 1;
    const detailsContainerWeight = tableData[0] ? tableData[0].weights.slice(1)
      .reduce((acc, cur) => acc + cur) : 1;

    // Calculate style for each column
    const getColStyle = (colIndex) => {
      if (columnWidths && columnWidths[colIndex] != null) {
        return { width: columnWidths[colIndex], minWidth: 50, flex: 'none' };
      }
      return { flex: headers[colIndex].weight };
    };

    return (
      <div className="table-with-details">

        <div
          className="table"
          style={{ minHeight: tableHeight }}
          ref={this.tableRef}
        >

          <div
            style={{ minHeight: rowHeight }}
            className="row header"
          >
            {
              headers.map((header, colIndex) => (
                <div
                  key={header.key}
                  style={getColStyle(colIndex)}
                >
                  <div className="cell" style={{ position: 'relative', userSelect: 'none' }}>
                    {header.alias || header.key}
                    {colIndex < headers.length - 1 && (
                      <div
                        className="col-resizer"
                        style={{
                          position: 'absolute',
                          right: 0,
                          top: 0,
                          height: '100%',
                          width: 6,
                          cursor: 'col-resize',
                          zIndex: 2,
                        }}
                        onMouseDown={e => this.handleResizeStart(e, colIndex)}
                      />
                    )}
                  </div>
                </div>
              ))
            }
          </div>

          {
            tableData.map((rowItem, rowIndex) => (
              <div
                key={rowItem.id}
                role="presentation"
                style={{ minHeight: rowHeight }}
                className={
                  `row
                  ${selectedRowIndex === rowIndex ? 'selected' : ' '}
                  ${hoverableRow ? 'hoverable-row' : ' '}
                  ${rowIndex % 2 === 0 && isStripes ? 'odd' : ' '}
                  `
                }
                onMouseDown={this.handleRowKeyDown}
                onMouseUp={e => this.handleRowKeyUp(e, rowIndex)}
              >
                {
                  rowItem.values.map((value, colIndex) => (
                    <div
                      key={rowItem.id + headers[colIndex].key}
                      style={{
                        ...getColStyle(colIndex),
                        minWidth: 0, // allow shrinking
                        maxWidth: '100%',
                        overflow: 'hidden',
                      }}
                    >
                      <div className="cell">{value}</div>
                    </div>
                  ))
                }
              </div>
            ))
          }

        </div>


        {
          isHaveDetails && (
            <div
              className={`details ${isExpanded ? 'expanded' : ''}`}
              style={{ top: `-${isExpanded ? expandHeight : 0}px` }}
            >
              {
                !isExpanded && <div style={{ flex: firstColumnWeight }} />
              }
              <div
                style={{ flex: detailsContainerWeight }}
                className="details-container"
              >
                <div
                  style={{ minHeight: rowHeight }}
                  className="details-container-header"
                >
                  <IconButton
                    onClick={this.handleExpand}
                  >
                    <img src={isExpanded ? collapseIcon : expandIcon} alt="" />
                  </IconButton>

                  <IconButton
                    className="close-button"
                    onClick={this.handleCloseDetails}
                  >
                    <img src={closeIcon} alt="" />
                  </IconButton>
                </div>
                <div className="details-container-body">
                  {children}
                </div>
              </div>
            </div>
          )
        }


      </div>
    );
  }
}

export default TableWithDetails;
