import PropTypes from "prop-types";

function Grid({cells}) {
    return (
        <table>
            <colgroup span="1"/>
            <colgroup className="region" span="3"/>
            <colgroup className="region" span="3"/>
            <colgroup className="region" span="3"/>

            <HeaderRow/>
            <tbody className="region">
            <Row row={0} cells={cells}/>
            <Row row={1} cells={cells}/>
            <Row row={2} cells={cells}/>
            </tbody>
            <tbody className="region">
            <Row row={3} cells={cells}/>
            <Row row={4} cells={cells}/>
            <Row row={5} cells={cells}/>
            </tbody>
            <tbody className="region">
            <Row row={6} cells={cells}/>
            <Row row={7} cells={cells}/>
            <Row row={8} cells={cells}/>
            </tbody>
        </table>
    );
}

export const GRID_SIZE = 9;
export const GRID_INDEXES = [0, 1, 2, 3, 4, 5, 6, 7, 8];
export const SEGMENT_TYPES = ['row', 'column', 'region'];
export const REGION_SIZE = 3;
export const REGION_INDEXES = [0, 1, 2];

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

function Row({row, cells}) {
    const rowLabel = "ABCDEFGHI".charAt(row);
    const rowStartIdx = row * GRID_SIZE;
    return (
        <tr>
            <th scope="row">{rowLabel}</th>
            {GRID_INDEXES.map((col) => {
                const cellIdx = rowStartIdx + col;
                return cells[cellIdx];
            })}
        </tr>
    );
}

Grid.propTypes = {
    cells: PropTypes.arrayOf(PropTypes.element),
};
Row.propTypes = {
    row: PropTypes.number,
    cells: PropTypes.arrayOf(PropTypes.element),
};

export default Grid;
