import axios, {AxiosInstance} from "axios";
import React from "react";
import {useStateMachine} from "little-state-machine";

const AxiosContext = React.createContext<AxiosInstance | null>(null);

function AxiosProvider({ children }: {children: JSX.Element}) {
    const { state } = useStateMachine();
    //JWT might be set when doing a page refresh
    const initJwt: string = state?.jwt;
    const [axiosState, setAxiosState] = React.useState(() => axios.create({
        //Fix so this comes from env vars
        baseURL: process.env.REACT_APP_API_BASE_URL,
        headers: {
            'Authorization': 'Bearer ' + initJwt
        }
    }));

    React.useEffect(() => {
        if (state?.jwt) {
            setAxiosState(() => axios.create({
                //Fix so this comes from env vars
                baseURL: process.env.REACT_APP_API_BASE_URL,
                headers: {
                    'Authorization': 'Bearer ' + state.jwt
                }
            }))
        }
    }, [state?.jwt])

    return (
        <AxiosContext.Provider value={axiosState}>
            {children}
        </AxiosContext.Provider>
    )
}

function useAxios() {
    const axiosInstance = React.useContext(AxiosContext);
    if (!axiosInstance) {
        throw new Error('useAxios calls must be wrapped in an AxiosProvider!');
    }
    return axiosInstance;
}

export { AxiosProvider, useAxios }