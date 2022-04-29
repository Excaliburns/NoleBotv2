import React from "react";
import {GlobalState, useStateMachine} from "little-state-machine";
import {useLocation, useNavigate} from "react-router";
import Spinner from "./shared/components/Spinner";
import {useAxios} from "./util/AxiosProvider";

function updateJwt(state: GlobalState, payload: string) {
    return {
        ...state,
        jwt: payload
    }
}

function OauthRedirect() {
    const location = useLocation();
    const clientCode = new URLSearchParams(location.search).get("code");

    const {actions, state} = useStateMachine({
        updateJwt
    });
    const axios = useAxios();
    const navigate = useNavigate();


    React.useEffect(() => {
        if (clientCode) {
            axios.post('/oauth/discord', {
                clientCode: clientCode
            })
                .then(response => {
                    actions.updateJwt(response.data);
                    console.log(response.data);
                    navigate('/');
                })
        }

    }, [clientCode])


    return (
        <div>
            <Spinner/>
            Doing some stuff.... You will be redirected shortly.
        </div>
    )
}

export default OauthRedirect;