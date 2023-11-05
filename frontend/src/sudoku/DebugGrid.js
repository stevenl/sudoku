import React, {useContext} from "react";
import PropTypes from "prop-types";
import {GridContext} from "./Context";
import Grid, {GRID_SIZE} from "./Grid";

function DebugGrid() {
    const cells = [...Array(GRID_SIZE ** 2).keys()]
        .map(i => <DebugCell key={i} index={i}/>);
    return <Grid className="sudoku debug" cells={cells}/>;
}

function DebugCell({index}) {
    const grid = useContext(GridContext);
    const cell = grid.cell(index);

    if (cell.value) {
        if (cell.readOnly) {
            return <td className="readonly">{cell.value}</td>;
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
