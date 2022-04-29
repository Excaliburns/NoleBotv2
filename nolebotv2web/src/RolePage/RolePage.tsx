import {GuildUser} from "../entities/JavaGenerated";
import React, {useEffect, useState} from "react";
import {optimizeSelect} from "../util/OptimizedMenuList";
import AsyncSelect from "react-select/async";
import {useStateMachine} from "little-state-machine";
import {useAxios} from "../util/AxiosProvider";

export default function RolePage() {
    const { state } = useStateMachine();
    const [debounce, setDebounce] = useState<{ cb?: () => void, delay?: number; }>({})
    const axios = useAxios();

    useEffect(() => {
        const {cb, delay} = debounce;
        if (cb) {
            const timeout = setTimeout(cb, delay);
            return () => clearTimeout(timeout);
        }
        return;
    }, [debounce])

    const loadOptions = React.useCallback((
        searchValue: string,
        callback: (items:{value: string, label: string}[]) => unknown
    ) => {
        setDebounce({
            cb: async () => {
                const response = await axios.get('/guilds/' + process.env.REACT_APP_MAIN_SERVER_ID + '/users/', {params: {"name": searchValue}})
                let entries: {value: string, label: string}[] = []
                response.data.forEach((u: GuildUser) => {
                    entries.push({
                        value: u.nickname.toLowerCase(),
                        label: u.nickname
                    })
                })
                callback(entries)
            },
            delay: 200
        })
    }, []);



    return (
        <AsyncSelect loadOptions={loadOptions} components={optimizeSelect.components} isMulti={true}/>
    )

}