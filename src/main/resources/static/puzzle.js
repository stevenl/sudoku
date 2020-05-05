"use strict"

const cells = document.querySelectorAll('.sudoku td input');
const hintsToggler = document.querySelector('#hints-toggler');
const debugToggler = document.querySelector('#debug-toggler');

function map(collection, callback, scope) {
    const mapped = [];
    for (let i = 0; i < collection.length; i++) {
        const elem = collection[i];
        const newElem = callback.call(scope, elem, i);
        mapped.push(newElem);
    }
    return mapped;
};

function showHints(hints) {
    const solvableCell = new Set(hints.solvableCells);
    for (let i = 0; i < cells.length; i++) {
        if (solvableCell.has(i)) {
            cells[i].classList.add('solvable');
        } else {
            cells[i].classList.remove('solvable');
        }
    }
}

function updateHints() {
    const gridValues = map(cells, (elem) => elem.value != '' ? elem.value : '0').join('');

    fetch(`/puzzles/hints?values=${gridValues}`)
        .then(res => res.json())
        .then(hints => showHints(hints));
}

function enableHints() {
    updateHints();

    for (const cell of cells) {
        cell.oninput = function () {
            cell.classList.remove('solvable');
            updateHints(cells);
        }
    }
}

function disableHints() {
    for (const cell of cells) {
        cell.classList.remove('solvable');
        cell.oninput = null;
    }
}

function toggleHints() {
    if (hintsToggler.checked) {
        enableHints();
    } else {
        disableHints();
    }
}

hintsToggler.addEventListener('input', toggleHints);
if (hintsToggler.checked) {
    enableHints();
}

const debug = document.querySelector('.debug');

function toggleDebug() {
    if (debugToggler.checked) {
        debug.style.display = 'block';
    } else {
        debug.style.display = 'none';
    }
}

debugToggler.addEventListener('input', toggleDebug);
if (debugToggler.checked) {
    toggleDebug();
}