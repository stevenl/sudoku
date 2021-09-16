import {GRID_INDEXES, GRID_SIZE} from './GridState';

export default function Grid(props) {
    return (
        <table>
            <colgroup span="1"/>
            <colgroup className="region" span="3"/>
            <colgroup className="region" span="3"/>
            <colgroup className="region" span="3"/>

            <HeaderRow/>
            <tbody className="region">
            <Row row={0} cells={props.cells}/>
            <Row row={1} cells={props.cells}/>
            <Row row={2} cells={props.cells}/>
            </tbody>
            <tbody className="region">
            <Row row={3} cells={props.cells}/>
            <Row row={4} cells={props.cells}/>
            <Row row={5} cells={props.cells}/>
            </tbody>
            <tbody className="region">
            <Row row={6} cells={props.cells}/>
            <Row row={7} cells={props.cells}/>
            <Row row={8} cells={props.cells}/>
            </tbody>
        </table>
    );
}

function HeaderRow() {
    return (
        <thead>
        <tr>
            <th>{/* empty row/column header */}</th>
            {GRID_INDEXES.map((i) =>
                <th scope="col" key={i}>{i}</th>,
            )}
        </tr>
        </thead>
    );
}

function Row(props) {
    const rowLabel = "ABCDEFGHI".charAt(props.row);
    const rowStartIdx = props.row * GRID_SIZE;
    return (
        <tr>
            <th scope="row">{rowLabel}</th>
            {GRID_INDEXES.map((col) => {
                const cellIdx = rowStartIdx + col;
                return props.cells[cellIdx];
            })}
        </tr>
    );
}
