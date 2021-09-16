import React from "react";
import GridState from "./GridState";

// noinspection JSUnusedLocalSymbols
export const DispatchContext = React.createContext(action => null);

export const GridContext = React.createContext(new GridState());
