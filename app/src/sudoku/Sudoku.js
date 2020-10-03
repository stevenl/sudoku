import React, { useContext, useReducer } from 'react';
import './Sudoku.css';

const GRID_SIZE = 9;
// const REGION_SIZE = 3;
const CELL_RANGE = [0, 1, 2, 3, 4, 5, 6, 7, 8];
const ROW_LABELS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'];

// noinspection JSUnusedLocalSymbols
const GridDispatch = React.createContext(action => null);

function reducer(grid, action) {
    const cellIdx = action.index;
    if (grid[cellIdx].readOnly) {
        return grid;
    }

    const newGrid = grid.slice();
    newGrid[cellIdx] = {value: action.value};
    return newGrid;
}

function Sudoku(props) {
    const initialGrid = parseGrid(props.gridString);
    const [grid, dispatch] = useReducer(reducer, initialGrid);

    function parseGrid(gridString) {
        const values = (gridString).split('');
        return values.map((v) => {
            if (!v || v < 1) {
                v = NaN;
            }
            const cell = {value: v};
            if (!isNaN(v)) {
                cell.readOnly = true;
            }
            return cell;
        });
    }

    return (
        <GridDispatch.Provider value={dispatch}>
            <div className="sudoku">
                <Grid grid={grid} />
            </div>
        </GridDispatch.Provider>
    );
}

function Grid(props) {
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
                <Row row={0} grid={props.grid} />
                <Row row={1} grid={props.grid} />
                <Row row={2} grid={props.grid} />
            </tbody>
            <tbody className="region">
                <Row row={3} grid={props.grid} />
                <Row row={4} grid={props.grid} />
                <Row row={5} grid={props.grid} />
            </tbody>
            <tbody className="region">
                <Row row={6} grid={props.grid} />
                <Row row={7} grid={props.grid} />
                <Row row={8} grid={props.grid} />
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
                return <Cell
                    key={cellIdx}
                    index={cellIdx}
                    cell={props.grid[cellIdx]}
                />;
            })}
        </tr>
    );
}

function Cell(props) {
    const cell = props.cell;
    const dispatch = useContext(GridDispatch);
    return (
        <td>
            <input
                type="number" min="1" max="9"
                value={cell.value ? cell.value : ''}
                readOnly={cell.readOnly}
                onChange={(event) => dispatch({
                    index: props.index,
                    value: event.target.valueAsNumber,
                })}
            />
        </td>
    );
}

export default Sudoku;
