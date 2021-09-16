import React, {useReducer} from 'react';
import {DispatchContext, GridContext} from './Context';
import {DebugGrid} from './DebugGrid';
import {PuzzleGrid} from './PuzzleGrid';
import {GridState} from './GridState';
import {gridReducer} from './gridReducer';
import './Sudoku.css';

export default function Sudoku(props) {
    const [grid, dispatch] = useReducer(
        gridReducer,
        props.gridString,
        (gridString) => GridState.newFrom(gridString),
    );

    return (
        <GridContext.Provider value={grid}>
            <DispatchContext.Provider value={dispatch}>
                <div className="sudoku">
                    <PuzzleGrid/>
                </div>
                <div className='sudoku debug'>
                    <DebugGrid />
                </div>
            </DispatchContext.Provider>
        </GridContext.Provider>
    );
}
