import React, { Dispatch, SetStateAction } from "react";
import { axiosDiscordInstance, axiosNolebotInstance } from "./util/AxiosNolebotInstance";
import { AccessToken } from "./entities/AccessToken";
import { GlobalState, useStateMachine } from "little-state-machine";
import { APIUser } from "discord-api-types";
import { useLocation, useNavigate } from "react-router";
import Spinner from "./shared/components/Spinner";

const updateUserToken = (state: GlobalState, payload: AccessToken) => ({
    ...state,
    userToken: {
        ...state.userToken,
        ...payload
    }
});

const updateUserDetails = (state: GlobalState, payload: APIUser) => (
    {
    ...state,
    userDetails: {
        ...state.userDetails,
        ...payload
    }
})

function OauthRedirect() {
    const location = useLocation();
    const clientCode = new URLSearchParams(location.search).get("code");

    const { actions, state } = useStateMachine({
        updateUserToken,
        updateUserDetails
    });

    const navigate = useNavigate();

    React.useEffect( () => {
        if (clientCode) {
            console.log(clientCode)
            axiosNolebotInstance.post('/login', {
                auth_token: clientCode
            }).then( response => {
                axiosNolebotInstance.defaults.headers.common['Authorization'] = "Bearer " + response.data.access_token
            })
        }

    }, [clientCode])

    React.useEffect(() => {
        if (state?.userToken) {

        }
    }, [state?.userToken])


    return (
        <div>
            <Spinner />
            Doing some stuff.... You will be redirected shortly.
        </div>
    )
}

export default OauthRedirect;