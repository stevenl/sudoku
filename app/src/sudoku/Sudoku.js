import React from 'react';
import './Sudoku.css';

const GRID_SIZE = 9;
// const REGION_SIZE = 3;
const CELL_RANGE = [0, 1, 2, 3, 4, 5, 6, 7, 8];
const ROW_LABELS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'];

class Sudoku extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            grid: this.parseGrid(props.gridString)
        };
    }

    parseGrid(gridString) {
        const values = (gridString).split('');
        return values.map((v) => {
            if (!v || v < 1) {
                v = NaN;
            }
            const cell = {value: v};
            if (!isNaN(v)) {
                cell.readOnly = true;
            }
            return cell;
        });
    }

    render() {
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
                        {this.renderRow(0)}
                        {this.renderRow(1)}
                        {this.renderRow(2)}
                    </tbody>
                    <tbody className="region">
                        {this.renderRow(3)}
                        {this.renderRow(4)}
                        {this.renderRow(5)}
                    </tbody>
                    <tbody className="region">
                        {this.renderRow(6)}
                        {this.renderRow(7)}
                        {this.renderRow(8)}
                    </tbody>
                </table>
            </div>
        );
    }

    renderRow(row) {
        return (
            <Row
                row={row}
                grid={this.state.grid}
                onChange={(idx, val) => this.handleCellChange(idx, val)}
            />
        );
    }

    handleCellChange(cellIdx, value) {
        // console.log(cellIdx, value);
        const cell = this.state.grid[cellIdx];
        if (cell.readOnly) {
            return;
        }

        const grid = this.state.grid.slice();
        grid[cellIdx] = {value: value};

        this.setState({grid: grid});
        console.log(grid);
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
                    key={cellIdx}
                    index={cellIdx}
                    cell={props.grid[cellIdx]}
                    onChange={props.onChange}
                />;
            })}
        </tr>
    );
}

function Cell(props) {
    const cell = props.cell;
    return (
        <td>
            <input
                type="number" min="1" max="9"
                value={cell.value ? cell.value : ''}
                readOnly={cell.readOnly}
                onChange={(e) => props.onChange(props.index, e.target.valueAsNumber)}
            />
        </td>
    );
}

export default Sudoku;
