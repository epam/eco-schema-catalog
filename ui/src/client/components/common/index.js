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
import PickerComponent from './components/Paginator/Picker/Picker';
import PaginatorComponent from './components/Paginator/Paginator';
import PaginatorInputComponent from './components/Paginator/Input/Input';
import PaginatorDelimeterComponent from './components/Paginator/Delimeter/Delimeter';
import PaginatorControlComponent from './components/Paginator/Control/Control';
import PaginatorLabelComponent from './components/Paginator/Label/Label';

import ButtonComponent from './components/Button/Button';
import IconButtonComponent from './components/IconButton/IconButton';
import DropDownButtonComponent from './components/DropDownButton/DropDownButton';

import InputComponent from './components/Input/Input';
import CheckboxComponent from './components/Checkbox/Checkbox';
import SelectorComponent from './components/Selector/Selector';

import AlertPopUpComponent from './components/AlertPopUp/AlertPopUp';
import LoaderComponent from './components/Loader/Loader';
import PanelComponent from './components/Panel/Panel';
import TabsContainerComponent from './components/TabsContainer/TabsContainer';
import TableWithDetailsComponent from './components/TableWithDetails/TableWithDetails';

import Modal from './components/Modal';
import ModalBody from './components/Modal/ModalBody/ModalBody';
import ModalHeader from './components/Modal/ModalHeader/ModalHeader';
import ModalFooter from './components/Modal/ModalFooter/ModalFooter';

import * as CORE from './consts/core';
import * as INPUT from './consts/input';
import * as PICKER from './consts/picker';
import * as PAGINATOR from './consts/paginator';
import * as LABELS from './consts/labels';
import * as CONTROLS from './consts/controls';
import * as DELIMETER from './consts/delimeter';

import * as functional from './utils/functional';
import * as paginator from './utils/paginator';
import * as string from './utils/string';

export const Picker = PickerComponent;
export const Paginator = PaginatorComponent;
export const PaginatorInput = PaginatorInputComponent;
export const Delimeter = PaginatorDelimeterComponent;
export const Control = PaginatorControlComponent;
export const Label = PaginatorLabelComponent;

export const Button = ButtonComponent;
export const IconButton = IconButtonComponent;
export const DropDownButton = DropDownButtonComponent;

export const Input = InputComponent;
export const Checkbox = CheckboxComponent;
export const Selector = SelectorComponent;

export const AlertPopUp = AlertPopUpComponent;
export const Loader = LoaderComponent;
export const Panel = PanelComponent;
export const TabsContainer = TabsContainerComponent;
export const TableWithDetails = TableWithDetailsComponent;

export const consts = {
  CORE, INPUT, PICKER, PAGINATOR, LABELS, CONTROLS, DELIMETER,
};
export const utils = { functional, paginator, string };
export const components = {
  Picker, Paginator, Input, Delimeter, Control, Label, Button,
};
export const modals = {
  Modal, ModalBody, ModalHeader, ModalFooter,
};
