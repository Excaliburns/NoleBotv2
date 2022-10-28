
import {useTheme, Theme} from '@mui/material/styles';
import {Virtuoso} from 'react-virtuoso'
import {GuildRole, GuildUser} from "../../entities/JavaGenerated";
import {useAxios} from "../../util/AxiosProvider";
import {Autocomplete, Avatar, Box, Button, TextField} from "@mui/material";
import {useStateMachine} from "little-state-machine";
import React, {useState} from "react";

const Listbox = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLElement>>((props, ref) => {
    const {children, ...other} = props;
    const itemData: React.ReactChild[] = [];
    (children as React.ReactChild[]).forEach(
        (item: React.ReactChild ) => {
            itemData.push(item);
        },
    );
    return <div {...other} ref={ref}>
        <Virtuoso
            style={{
                height: "60vh"
            }}
            totalCount={itemData.length}
            itemContent={(index) => {
                return itemData[index]
            }
        }
        />
    </div>
})
export default function RoleAssigner() {
    const mainBoxStyle = {
        margin: "1vh",
        "& .MuiFormControl-root": {
            margin: "1vh",
        },
    }
    const [memberOptions, setMemberOptions] = useState<{value: string, id: string, iconUrl: string}[]>([])
    const [roleOptions, setRoleOptions] = useState<{value: string, id: string}[]>([])
    const [search, setSearch] = useState<string>("")
    let [selectedMembers, setSelectedMembers] = React.useState<{value: string, id: string}[]>([])
    let [selectedRoles, setSelectedRoles] = React.useState<{value: string, id: string}[]>([])
    const {state} = useStateMachine()
    const axios = useAxios()
    const clickButton = () => {
        let roleIds: string[] = []
        let userIds: string[] = []
        selectedRoles.forEach(r => {
            roleIds.push(r.id)
        })
        selectedMembers.forEach(u => {
            userIds.push(u.id)
        })
        axios.post("/bot/assign_roles", {
            roleIds: roleIds,
            userIds: userIds,
            guildId: process.env.REACT_APP_MAIN_SERVER_ID
        })
    }

    React.useEffect(() => {
        if (search != ""){
            axios.get('/guilds/' + process.env.REACT_APP_MAIN_SERVER_ID + '/users/', {params: {"name": search}}).then((response) => {
                let entries: {value: string, id: string, iconUrl: string}[] = []
                response.data.forEach((u: GuildUser) => {
                    entries.push({
                        value: u.nickname,
                        id: u.id,
                        iconUrl: u.avatarUrl
                    })
                })
                setMemberOptions(entries)
            })
        }
    }, [search])

    React.useEffect(() => {
        axios.get('/guilds/' + process.env.REACT_APP_MAIN_SERVER_ID + '/assignable_roles')
            .then((response) => {
                let entries: {value: string, id:string}[] = []
                response.data.forEach((g: GuildRole) => {
                    entries.push({
                        value: g.name,
                        id: g.id
                    })
                })
                setRoleOptions(entries)
            })
    }, [state.jwt])
    return (
        <Box sx={mainBoxStyle}>
            <Autocomplete
                multiple={true}
                renderInput={
                    (params) => {
                        return <TextField {...params} label={"Roles"}/>
                    }
                }
                options={roleOptions}
                getOptionLabel={(option) => {
                    return option.value
                }}
                ListboxComponent={Listbox}
                onInputChange={(event, newVal, reason) => {
                    setSearch(newVal)
                }}
                onChange={(event, value, reason) => {
                    setSelectedRoles(value)
                }}/>

            <Autocomplete
                multiple={true}
                renderInput={
                    (params) => {
                        return <TextField {...params} label={"Members"}/>
                    }
                }
                options={memberOptions}
                getOptionLabel={(option) => {
                    return option.value
                }}
                ListboxComponent={Listbox}
                renderOption={(props:object, option:{value: string, id:string, iconUrl: string}) => {
                    return <Box {...props} sx={{
                        display: "flex",
                        paddingX: "0!important",
                    }}>
                        <Avatar src={option.iconUrl} sx={{
                            marginX: "1vw"
                        }}/>
                        {option.value}
                    </Box>
                }}
                onInputChange={(event, newVal, reason) => {
                    setSearch(newVal)
                }}
                onChange={(event, value, reason) => {
                    setSelectedMembers(value)
                }}/>
            <Box sx={{display: "flex", justifyContent: "right"}}>
                <Button variant={"contained"} onClick={clickButton}>Submit</Button>
            </Box>
        </Box>
    );
}
