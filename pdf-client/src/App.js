import React from "react";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import "./App.css";

import { UploadPDF } from "./upload/Upload";
import { Main } from "./Main";
import {ViewContainer} from "./Viewer/Container"

function App() {
  return (
    <div className={"App-header"}>
      <Router>
        <Switch>
           <Route path={"/upload"} component={UploadPDF} />
           <Route path={"/view"} component={ViewContainer} />
          <Route path="/" component={Main} />
        </Switch>
      </Router>
    </div>
  );
}

export default App;
