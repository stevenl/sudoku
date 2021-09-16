import {useContext} from "react";
import {DispatchContext, GridContext} from "./Context";
import {Grid} from "./Grid";
import {GRID_INDEXES, GRID_SIZE} from "./GridState";
import {SetValueAction} from "./gridReducer";

export function PuzzleGrid() {
    let cells = [];
    for (const row of GRID_INDEXES) {
        for (const col of GRID_INDEXES) {
            const cellIdx = (row * GRID_SIZE) + col;
            // console.log("index = ", row, col, GRID_SIZE, cellIdx);
            const cell = <Cell key={cellIdx} index={cellIdx}/>;
            cells.push(cell);
        }
    }
    return <Grid cells={cells}/>;
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
