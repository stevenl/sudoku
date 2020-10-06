import React, { useContext, useReducer } from 'react';
import { CELL_RANGE, GridState, GRID_SIZE, gridReducer } from './grid';
import './Sudoku.css';

const ROW_LABELS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'];

const GridContext = React.createContext(new GridState());
// noinspection JSUnusedLocalSymbols
const DispatchContext = React.createContext(action => null);

function Sudoku(props) {
    const [grid, dispatch] = useReducer(gridReducer, props.gridString, (gridString) => new GridState(gridString));

    return (
        <GridContext.Provider value={grid}>
            <DispatchContext.Provider value={dispatch}>
                <div className="sudoku">
                    <Grid />
                </div>
            </DispatchContext.Provider>
        </GridContext.Provider>
    );
}

function Grid() {
    return (
        <table>
            <caption>Sudoku</caption>

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

            <HeaderRow />
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

function HeaderRow() {
    return (
        <thead>
            <tr>
                <th>{/* empty row/column header */}</th>
                {CELL_RANGE.map((i) =>
                    <th scope="col" key={i}>{i + 1}</th>,
                )}
            </tr>
        </thead>
    );
}

function Row(props) {
    const startIdx = props.row * GRID_SIZE;
    const rowLabel = ROW_LABELS[props.row];
    return (
        <tr>
            <th scope="row">{rowLabel}</th>
            {CELL_RANGE.map((i) => {
                const cellIdx = startIdx + i;
                return <Cell key={cellIdx} index={cellIdx} />;
            })}
        </tr>
    );
}

function Cell(props) {
    const dispatch = useContext(DispatchContext);
    const grid = useContext(GridContext);
    const cell = grid[props.index];

    if (cell.readOnly) {
        return <td>{cell.value}</td>;
    }

    return (
        <td>
            <input
                type="number" min="1" max="9" maxLength="1"
                value={cell.value ? cell.value : ''}
                onChange={(event) => (
                    dispatch({
                        type: 'setValue',
                        index: cell.index,
                        value: event.target.valueAsNumber,
                    })
                )}
                // className={cell.error ? 'error' : ''}
            />
        </td>
    );
}

export default Sudoku;
