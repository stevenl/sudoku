import {useContext} from "react";
import PropTypes from "prop-types";
import {DispatchContext, GridContext} from "./Context";
import Grid, {GRID_INDEXES, GRID_SIZE} from "./Grid";
import {SetValueAction} from "./gridReducer";

function PuzzleGrid() {
    let cells = [];
    for (const row of GRID_INDEXES) {
        for (const col of GRID_INDEXES) {
            const cellIdx = (row * GRID_SIZE) + col;
            const cell = <Cell key={cellIdx} index={cellIdx}/>;
            cells.push(cell);
        }
    }
    return <Grid cells={cells}/>;
}

function Cell({index}) {
    const dispatch = useContext(DispatchContext);
    const grid = useContext(GridContext);
    const cell = grid.cells[index];

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

Cell.propTypes = {
    index: PropTypes.number
};

export default PuzzleGrid;
