import {useContext} from "react";
import PropTypes from "prop-types";
import {DispatchContext, GridContext} from "./Context";
import Grid, {GRID_SIZE} from "./Grid";
import {SetValueAction} from "./gridReducer";

function PuzzleGrid() {
    const cells = [...Array(GRID_SIZE ** 2).keys()]
        .map(i => <Cell key={i} index={i}/>);
    return <Grid className="sudoku" cells={cells}/>;
}

function Cell({index}) {
    const dispatch = useContext(DispatchContext);
    const grid = useContext(GridContext);
    const cell = grid.cell(index);

    if (cell.readOnly) {
        return <td className="readonly">{cell.value}</td>;
    }

    return (
        <td>
            <input
                type="number" min="1" max="9" maxLength="1"
                value={cell.value || ''}
                onChange={event =>
                    dispatch(new SetValueAction(cell.index, event.target.valueAsNumber, grid.cells))
                }
                // A hack to prevent mouse scrolling messing up the availableValues
                onWheel={e => e.target.blur()}
                className={`${cell.availableValues.size === 1 ? 'hint' : ''} ${cell.errors.total > 0 ? 'error' : ''}`}
            />
        </td>
    );
}

Cell.propTypes = {
    index: PropTypes.number
};

export default PuzzleGrid;
