import {GuildRole, GuildUser} from "../../entities/JavaGenerated";
import React, {useEffect, useState} from "react";
import {optimizeSelect} from "../../util/OptimizedMenuList";
import AsyncSelect from "react-select/async";
import {useStateMachine} from "little-state-machine";
import {useAxios} from "../../util/AxiosProvider";
import Select from "react-select";
import {useNavigate} from "react-router";

export default function RoleAssigner() {
    const { state } = useStateMachine();
    const [debounce, setDebounce] = useState<{ cb?: () => void, delay?: number; }>({})
    const [selectedUsers, setSelectedUsers] = useState<ReadonlyArray<any>>([])
    const [roleOptions, setRoleOptions] = useState<{value: string, label: string}[]>([])
    const [selectedRoles, setSelectedRoles] = useState<ReadonlyArray<any>>([])
    const axios = useAxios();
    const navigate = useNavigate()

    const onRoleChange = (newOptions: ReadonlyArray<any>) => {
        setSelectedRoles(newOptions)
    }
    const onUserChange = (newOptions: ReadonlyArray<any>) => {
        setSelectedUsers(newOptions)
    }
    const clickButton = () => {
        let roleIds: string[] = []
        let userIds: string[] = []
        selectedRoles.forEach(r => {
            roleIds.push(r.value)
        })
        selectedUsers.forEach(u => {
            userIds.push(u.value)
        })
        axios.post("/bot/assign_roles", {
            roleIds: roleIds,
            userIds: userIds,
            guildId: process.env.REACT_APP_MAIN_SERVER_ID
        })
    }
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
                        value: u.id,
                        label: u.nickname
                    })
                })
                callback(entries)
            },
            delay: 200
        })
    }, []);

    React.useEffect(() => {
        axios.get('/guilds/' + process.env.REACT_APP_MAIN_SERVER_ID + '/assignable_roles')
            .then((response) => {
                let entries: {value: string, label:string}[] = []
                response.data.forEach((g: GuildRole) => {
                    entries.push({
                        value: g.id,
                        label: g.name
                    })
                })
                setRoleOptions(entries)
            })
    }, [state.jwt])

    React.useEffect(() => {
        if (!state?.jwt) {
            navigate("/login")
        }
    })



    return (
        <>
            <Select isMulti={true} onChange={onRoleChange} options={roleOptions}/>
            <AsyncSelect loadOptions={loadOptions} components={optimizeSelect.components} isMulti={true} onChange={onUserChange}/>
            <button onClick={clickButton}>Submit</button>
        </>
    )

}