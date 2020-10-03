import React, { useContext, useReducer } from 'react';
import { emptyGrid, GRID_SIZE, gridReducer, parseGrid } from './grid';
import './Sudoku.css';

// const REGION_SIZE = 3;
const CELL_RANGE = [0, 1, 2, 3, 4, 5, 6, 7, 8];
const ROW_LABELS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'];

const GridState = React.createContext(emptyGrid());
// noinspection JSUnusedLocalSymbols
const GridDispatch = React.createContext(action => null);

function Sudoku(props) {
    const initialGrid = parseGrid(props.gridString);
    const [grid, dispatch] = useReducer(gridReducer, initialGrid);

    return (
        <GridState.Provider value={grid}>
            <GridDispatch.Provider value={dispatch}>
                <div className="sudoku">
                    <Grid />
                </div>
            </GridDispatch.Provider>
        </GridState.Provider>
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
    const dispatch = useContext(GridDispatch);
    const grid = useContext(GridState);
    const cell = grid[props.index];
    return (
        <td>
            <input
                type="number" min="1" max="9"
                value={cell.value ? cell.value : ''}
                readOnly={cell.readOnly}
                onChange={(event) => (
                    dispatch({
                        index: cell.index,
                        value: event.target.valueAsNumber,
                    })
                )}
            />
        </td>
    );
}

export default Sudoku;
