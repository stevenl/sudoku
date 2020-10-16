import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import * as serviceWorker from './serviceWorker';
import Sudoku from './sudoku/Sudoku';

const gridString = '000483276600102580020000100006007000130809047000600900008000060057201008469578000';
// const gridString = '000000080005073090000900300000200709900136004403009000001005000060840900070000000';

ReactDOM.render(
    <React.StrictMode>
        <Sudoku gridString={gridString} />
    </React.StrictMode>,
    document.getElementById('root'),
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
