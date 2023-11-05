import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import * as serviceWorker from './serviceWorker';
import Sudoku from './sudoku/Sudoku';

// const gridString = '000483276600102580020000100006007000130809047000600900008000060057201008469578000'; // easy
// const gridString = '070001000005009003103074000608000030901000207020000908000950602400300500000700080'; // medium
// const gridString = '000090008000000010413706002004900003090040050600008400800509741020000000500010000'; // hard
const gridString = ' 00000080005073090000900300000200709900136004403009000001005000060840900070000000'; // evil

ReactDOM.render(
    <React.StrictMode>
        <p><a href="https://github.com/stevenl/sudoku/" target="_blank" rel="noopener noreferrer">Source on GitHub</a>
        </p>
        <Sudoku gridString={gridString}/>
    </React.StrictMode>,
    document.getElementById('root'),
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
