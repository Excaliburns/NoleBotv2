import React from "react";
import {GlobalState, useStateMachine} from "little-state-machine";
import {useLocation, useNavigate} from "react-router";
import Spinner from "./shared/components/Spinner";
import {useAxios} from "./util/AxiosProvider";
import {DiscordUser} from "./entities/JavaGenerated";

const updateJwt = (state: GlobalState, newJwt: string) => ({
    ...state,
    jwt: newJwt
});
const updateUserDetails = (state: GlobalState, payload: DiscordUser) => ({
    ...state,
    userDetails: {
        ...state.userDetails,
        ...payload
    }
});

function OauthRedirect() {
    const location = useLocation();
    const clientCode = new URLSearchParams(location.search).get("code");

    const {actions, state} = useStateMachine({
        updateJwt,
        updateUserDetails
    });
    const axios = useAxios();
    const navigate = useNavigate();


    React.useEffect(() => {
        if (clientCode) {
            console.log(clientCode)
            axios.post('/login', {
                auth_token: clientCode
            }).then( response => {
                actions.updateJwt(response.data.access_token)
                navigate("/")
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