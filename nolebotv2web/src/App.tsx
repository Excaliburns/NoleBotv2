import { Link, Route, Router, Switch } from "react-router-dom";
import MainPage from "./MainPage";
import OauthRedirect from "./OauthRedirect";

function App() {
    return (
        <Router>
            <div>
                <nav>
                    <ul>
                        <li>
                            <Link to="/">Home</Link>
                        </li>
                    </ul>
                </nav>

                {/* A <Switch> looks through its children <Route>s and
            renders the first one that matches the current URL. */}
                <Switch>
                    <Route path="/auth/redirect">
                        <OauthRedirect />
                    </Route>
                    <Route path="/">
                        <MainPage />
                    </Route>
                </Switch>
            </div>
        </Router>
    )
}

export default App;