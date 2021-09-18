import React, {useReducer} from 'react';
import PropTypes from "prop-types";
import {DispatchContext, GridContext} from './Context';
import DebugGrid from './DebugGrid';
import PuzzleGrid from './PuzzleGrid';
import GridState from './GridState';
import {gridReducer} from './gridReducer';
import './Sudoku.css';

function Sudoku({gridString}) {
    const [grid, dispatch] = useReducer(
        gridReducer, gridString, GridState.newFrom
        // The 3rd arg for lazy initialization prevents repeated calls to
        // dispatch() on the initial cells
    );

    return (
        <GridContext.Provider value={grid}>
            <DispatchContext.Provider value={dispatch}>
                <PuzzleGrid/>
                <DebugGrid/>
            </DispatchContext.Provider>
        </GridContext.Provider>
    );
}

Sudoku.propTypes = {
    gridString: PropTypes.string
};

export default Sudoku;
