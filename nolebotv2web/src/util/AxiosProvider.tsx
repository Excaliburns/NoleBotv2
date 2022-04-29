import axios, {AxiosInstance} from "axios";
import React from "react";
import {useStateMachine} from "little-state-machine";

const AxiosContext = React.createContext<AxiosInstance | null>(null);

function AxiosProvider({ children }: {children: JSX.Element}) {
    const { state } = useStateMachine();
    const [axiosState, setAxiosState] = React.useState(() => axios.create({
        baseURL: 'http://localhost:8080/',
    }));

    React.useEffect(() => {
        if (state?.jwt) {
            setAxiosState(() => axios.create({
                baseURL: 'http://localhost:8080/',
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