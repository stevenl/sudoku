import React, { useContext, useReducer } from 'react';
import { GridState, GRID_INDEXES, GRID_SIZE } from './grid';
import { gridReducer, SetValueAction } from './gridReducer';
import './Sudoku.css';

const GridContext = React.createContext(new GridState());
// noinspection JSUnusedLocalSymbols
const DispatchContext = React.createContext(action => null);

export default function Sudoku(props) {
    const [grid, dispatch] = useReducer(
        gridReducer,
        props.gridString,
        (gridString) => new GridState().init(gridString),
    );

    return (
        <GridContext.Provider value={grid}>
            <DispatchContext.Provider value={dispatch}>
                <div className="sudoku">
                    <Grid />
                </div>
                <div className='sudoku debug'>
                    <DebugGrid />
                </div>
            </DispatchContext.Provider>
        </GridContext.Provider>
    );
}

function Grid() {
    return (
        <table>
            <colgroup className="region">
                <col span="3" />
            </colgroup>
            <colgroup className="region">
                <col span="3" />
            </colgroup>
            <colgroup className="region">
                <col span="3" />
            </colgroup>

            <tbody className="region">
                <Row row={0} />
                <Row row={1} />
                <Row row={2} />
            </tbody>
            <tbody className="region">
                <Row row={3} />
                <Row row={4} />
                <Row row={5} />
            </tbody>
            <tbody className="region">
                <Row row={6} />
                <Row row={7} />
                <Row row={8} />
            </tbody>
        </table>
    );
}

function Row(props) {
    const startIdx = props.row * GRID_SIZE;
    return (
        <tr>
            {GRID_INDEXES.map((offset) => {
                const cellIdx = startIdx + offset;
                return <Cell key={cellIdx} index={cellIdx} />;
            })}
        </tr>
    );
}

function Cell(props) {
    const dispatch = useContext(DispatchContext);
    const grid = useContext(GridContext);
    const cell = grid.cells[props.index];

    if (cell.readOnly) {
        return <td className={'readonly'}>{cell.value}</td>;
    }

    return (
        <td>
            <input
                type="number" min="1" max="9" maxLength="1"
                value={cell.value ? cell.value : ''}
                onChange={(event) => (
                    dispatch(new SetValueAction(cell.index, event.target.valueAsNumber))
                )}
                className={`${cell.availableValues.size === 1 ? 'hint' : ''} ${cell.errors.total > 0 ? 'error' : ''}`}
            />
        </td>
    );
}

// Debugger

function DebugGrid() {
    return (
        <table>
            <colgroup>
                <col />
            </colgroup>
            <colgroup className="region">
                <col span="3" />
            </colgroup>
            <colgroup className="region">
                <col span="3" />
            </colgroup>
            <colgroup className="region">
                <col span="3" />
            </colgroup>

            <DebugHeaderRow />
            <tbody className="region">
                <DebugRow row={0} />
                <DebugRow row={1} />
                <DebugRow row={2} />
            </tbody>
            <tbody className="region">
                <DebugRow row={3} />
                <DebugRow row={4} />
                <DebugRow row={5} />
            </tbody>
            <tbody className="region">
                <DebugRow row={6} />
                <DebugRow row={7} />
                <DebugRow row={8} />
            </tbody>
        </table>
    );
}

function DebugHeaderRow() {
    return (
        <thead>
            <tr>
                <th>{/* empty row/column header */}</th>
                {GRID_INDEXES.map((i) =>
                    <th scope="col" key={i}>{i}</th>,
                )}
            </tr>
        </thead>
    );
}

function DebugRow(props) {
    const startIdx = props.row * GRID_SIZE;
    return (
        <tr>
            <th scope="row">{startIdx}</th>
            {GRID_INDEXES.map((offset) => {
                const cellIdx = startIdx + offset;
                return <DebugCell key={cellIdx} index={cellIdx} />;
            })}
        </tr>
    );
}

function DebugCell(props) {
    const grid = useContext(GridContext);
    const cell = grid.cells[props.index];

    if (!isNaN(cell.value)) {
        if (cell.readOnly) {
            return <td className={'readonly'}>{cell.value}</td>;
        } else {
            return <td>{cell.value}</td>;
        }
    }
    return (
        <td className={`${cell.availableValues.size === 1 ? 'hint' : 'unsolved'}`}>
            {Array.from(cell.availableValues).sort().join(' ')}
        </td>
    );
}
