import React from 'react';
import './Sudoku.css';

const GRID_SIZE = 9;
// const REGION_SIZE = 3;
const CELL_RANGE = [0, 1, 2, 3, 4, 5, 6, 7, 8];
const ROW_LABELS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'];

class Sudoku extends React.Component {
    constructor(props) {
        super(props);
        const game = (props.game).split('');
        this.state = {
            game: game,
            cells: game.slice(),
        };
    }

    render() {
        const game = this.state.game;
        const cells = this.state.cells;
        return (
            <div className="sudoku">
                <table>
                    <caption>Sudoku</caption>

                    <colgroup>
                        <col />
                    </colgroup>
                    <colgroup className="region">
                        <col span="3" />
                    </colgroup>
                    <colgroup className="region">
                        <col span="3" />
                    </colgroup>
                    <colgroup className="region">
                        <col span="3" />
                    </colgroup>

                    <HeaderRow />
                    <tbody className="region">
                    <Row row="0" game={game} cells={cells} />
                    <Row row="1" game={game} cells={cells} />
                    <Row row="2" game={game} cells={cells} />
                    </tbody>
                    <tbody className="region">
                    <Row row="3" game={game} cells={cells} />
                    <Row row="4" game={game} cells={cells} />
                    <Row row="5" game={game} cells={cells} />
                    </tbody>
                    <tbody className="region">
                    <Row row="6" game={game} cells={cells} />
                    <Row row="7" game={game} cells={cells} />
                    <Row row="8" game={game} cells={cells} />
                    </tbody>
                </table>
            </div>
        );
    }
}

function HeaderRow() {
    return (
        <thead>
        <tr>
            <th>{/* empty row/column header */}</th>
            {CELL_RANGE.map((i) =>
                <th scope="col" key={i}>{i + 1}</th>,
            )}
        </tr>
        </thead>
    );
}

function Row(props) {
    const startIdx = props.row * GRID_SIZE;
    const rowLabel = ROW_LABELS[props.row];
    return (
        <tr>
            <th scope="row">{rowLabel}</th>
            {CELL_RANGE.map((i) => {
                const cellIdx = startIdx + i;
                return <Cell
                    key={i}
                    index={cellIdx}
                    value={props.cells[cellIdx]}
                    readOnly={props.game[cellIdx] > 0}
                />;
            })}
        </tr>
    );
}

function Cell(props) {
    return (
        <td>
            <input
                type="number" min="1" max="9"
                id={'cell-' + props.index}
                value={props.value > 0 ? props.value : ''}
                readOnly={props.readOnly}
            />
        </td>
    );
}

export default Sudoku;
