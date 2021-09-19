import React, {useContext} from "react";
import PropTypes from "prop-types";
import {GridContext} from "./Context";
import Grid, {GRID_INDEXES, GRID_SIZE} from "./Grid";

function DebugGrid() {
    let cells = [];
    for (const row of GRID_INDEXES) {
        for (const col of GRID_INDEXES) {
            const cellIdx = (row * GRID_SIZE) + col;
            // console.log("index = ", row, col, GRID_SIZE, cellIdx);
            const cell = <DebugCell key={cellIdx} index={cellIdx}/>;
            cells.push(cell);
        }
    }
    return <Grid className="sudoku debug" cells={cells}/>;
}

function DebugCell({index}) {
    const grid = useContext(GridContext);
    const cell = grid.cells[index];

    if (cell.value) {
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

DebugCell.propTypes = {
    index: PropTypes.number
};

export default DebugGrid;
