import {Autocomplete, Avatar, Box, TextField} from "@mui/material";
import getListbox from "../../shared/components/VirtualizedListbox";
import React, {useState} from "react";
import {useAxios} from "../../util/AxiosProvider";
import {Category, GuildRole, GuildUser, Owner, Role} from "../../entities/JavaGenerated";

type MemberRow = {
    name: string,
    id: string,
    iconUrl?: string
}

type RoleRow = {
    name: string,
    id: string,
    iconUrl?: string
}

export default function CategoryPage() {
    const [categories, setCategories] = useState<Category[]>([])
    const [selectedCategory, setSelectedCategory] = useState<Category | null>(null)
    const [availableRoles, setAvailableRoles] = useState<RoleRow[]>([])
    const [selectedRoles, setSelectedRoles] = useState<RoleRow[]>([])
    const [owners, setOwners] = useState<MemberRow[]>([])
    const [search, setSearch] = useState<string>("")
    const [selectedOwners, setSelectedOwners] = useState<MemberRow[]>([])
    const axios = useAxios()

    React.useEffect(() => {
        axios.get("/category/" + process.env.REACT_APP_MAIN_SERVER_ID +"/list").then((response) => {
            setCategories(response.data)
        })
        axios.get<GuildRole[]>("/guilds/" + process.env.REACT_APP_MAIN_SERVER_ID +"/all_roles").then((response) => {
            let guildRoles: RoleRow[] = []
            response.data.forEach((role: GuildRole) => {
                guildRoles.push({
                    name: role.name,
                    id: role.id,
                    iconUrl: role.iconLink
                })
            })
            setAvailableRoles(guildRoles)
        })
    }, [])

    React.useEffect(() => {
        axios.get("/guilds/" + process.env.REACT_APP_MAIN_SERVER_ID +"/users/", {params: {
                "name": search
            }}).
        then((response) => {
            let newMembers: MemberRow[] = []
            response.data.forEach((user: GuildUser) => {
                newMembers.push({
                    id: user.id,
                    name: user.nickname,
                    iconUrl: user.avatarUrl
                })
            })
            setOwners(newMembers)
        })
    }, [search])

    return <Box>
        <Autocomplete
        renderInput={
            (params) => {
                return <TextField {...params} label={"Role Categories"}/>
            }
        }
        options={categories}
        getOptionLabel={(option: Category) => {
            return option.categoryName
        }}
        ListboxComponent={getListbox()}
        onChange={(event, value, reason) => {
            console.log(value)
            setSelectedCategory(value)
            let newSelectedRoles = []
            if (value?.roles) {
                let guildRoles: RoleRow[] = []
                value.roles.forEach((role: Role) => {
                    guildRoles.push({
                        name: role.roleName,
                        id: role.id
                    })
                })
                setSelectedRoles(guildRoles)
            }
            else {
                setSelectedRoles([])
            }
            if (value?.owners) {
                console.log(value.owners)
                let newOwners: MemberRow[] = []
                value.owners.forEach((owner) => {
                    newOwners.push({
                        name: owner.ownerName,
                        id: owner.id
                    })
                })
                console.log(newOwners)
                setSelectedOwners(newOwners)
            }
            else {
                (setSelectedOwners([]))
            }

        }}/>
        <Autocomplete
            multiple={true}
            disabled={selectedCategory == null}
            renderInput={
                (params) => {
                    return <TextField {...params} label={"Roles"}/>
                }
            }
            options={availableRoles}
            value={selectedRoles}
            getOptionLabel={(option: RoleRow) => {
                return option.name
            }}
            ListboxComponent={getListbox()}
            onChange={(event, value, reason) => {
                setSelectedRoles(value)
            }}
            renderOption={(props:object, option: RoleRow) =>{
                return <Box {...props} sx={{
                    display: "flex",
                    paddingX: "0!important",
                }}>
                    <Avatar variant="square" src={option.iconUrl} sx={{
                        marginX: "1vw"
                    }}/>
                    {option.name}
                </Box>
            }}/>
        <Autocomplete
            multiple={true}
            disabled={selectedCategory == null}
            renderInput={
                (params) => {
                    return <TextField {...params} label={"Users"}/>
                }
            }
            options={owners}
            value={selectedOwners}
            getOptionLabel={(option: MemberRow) => {
                return option.name
            }}
            ListboxComponent={getListbox()}
            filterOptions={(x) => x}
            onInputChange={(event, newVal, reason) => {
                setSearch(newVal)
            }}
            onChange={(event, value, reason) => {
                setSelectedOwners(value)
            }}
            renderOption={(props:object, option: MemberRow) =>{
                return <Box {...props} sx={{
                    display: "flex",
                    paddingX: "0!important",
                }}>
                    <Avatar src={option.iconUrl} sx={{
                        marginX: "1vw"
                    }}/>
                    {option.name}
                </Box>
            }}/>
    </Box>

}