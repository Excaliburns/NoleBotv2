import * as React from 'react';
import TextField from '@mui/material/TextField';
import Autocomplete, { autocompleteClasses } from '@mui/material/Autocomplete';
import useMediaQuery from '@mui/material/useMediaQuery';
import ListSubheader from '@mui/material/ListSubheader';
import Popper from '@mui/material/Popper';
import { useTheme, styled } from '@mui/material/styles';
import { VariableSizeList, ListChildComponentProps } from 'react-window';
import Typography from '@mui/material/Typography';
import {useState} from "react";
import {GuildRole, GuildUser} from "../../entities/JavaGenerated";
import {useAxios} from "../../util/AxiosProvider";
import {Box, Button, SvgIcon} from "@mui/material";
import {useStateMachine} from "little-state-machine";

const LISTBOX_PADDING = 8; // px

function renderRow(props: ListChildComponentProps) {
    const { data, index, style } = props;
    const dataSet = data[index];
    const inlineStyle = {
        top: (style.top as number) + LISTBOX_PADDING,
        flexGrow: 1
    };

    if (dataSet.hasOwnProperty('group')) {
        return (
            <ListSubheader key={dataSet.key} component="div" style={inlineStyle}>
                {dataSet.group}
            </ListSubheader>
        );
    }

    return (
        <div style={{
            display: "flex",
            alignItems: "center"
        }}>
            <SvgIcon>
                <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z" />
            </SvgIcon>
            <Typography component="li" {...dataSet[0]} noWrap style={inlineStyle}>
                {dataSet[1]}
            </Typography>
        </div>
    );
}

const OuterElementContext = React.createContext({});

const OuterElementType = React.forwardRef<HTMLDivElement>((props, ref) => {
    const outerProps = React.useContext(OuterElementContext);
    return <div ref={ref} {...props} {...outerProps} />;
});

function useResetCache(data: any) {
    const ref = React.useRef<VariableSizeList>(null);
    React.useEffect(() => {
        if (ref.current != null) {
            ref.current.resetAfterIndex(0, true);
        }
    }, [data]);
    return ref;
}

// Adapter for react-window
const ListboxComponent = React.forwardRef<
    HTMLDivElement,
    React.HTMLAttributes<HTMLElement>
    >(function ListboxComponent(props, ref) {
    const { children, ...other } = props;
    const itemData: React.ReactChild[] = [];
    (children as React.ReactChild[]).forEach(
        (item: React.ReactChild & { children?: React.ReactChild[] }) => {
            itemData.push(item);
            itemData.push(...(item.children || []));
        },
    );

    const theme = useTheme();
    const smUp = useMediaQuery(theme.breakpoints.up('sm'), {
        noSsr: true,
    });
    const itemCount = itemData.length;
    const itemSize = smUp ? 36 : 48;

    const getChildSize = (child: React.ReactChild) => {
        if (child.hasOwnProperty('group')) {
            return 48;
        }

        return itemSize;
    };

    const getHeight = () => {
        if (itemCount > 8) {
            return 8 * itemSize;
        }
        return itemData.map(getChildSize).reduce((a, b) => a + b, 0);
    };

    const gridRef = useResetCache(itemCount);

    return (
        <div ref={ref}>
            <OuterElementContext.Provider value={other}>
                <VariableSizeList
                    itemData={itemData}
                    height={getHeight() + 2 * LISTBOX_PADDING}
                    width="100%"
                    ref={gridRef}
                    outerElementType={OuterElementType}
                    innerElementType="ul"
                    itemSize={(index) => getChildSize(itemData[index])}
                    overscanCount={5}
                    itemCount={itemCount}
                >
                    {renderRow}
                </VariableSizeList>
            </OuterElementContext.Provider>
        </div>
    );
});

const StyledPopper = styled(Popper)({
    [`& .${autocompleteClasses.listbox}`]: {
        boxSizing: 'border-box',
        '& ul': {
            padding: 0,
            margin: 0,
        },
    },
});

export default function RoleAssigner() {
    const selectStyle = {
        inputProps: {
            width: "100vw",
            marginY: "1vh",
            backgroundColor: "white",
        },
    }
    const [memberOptions, setMemberOptions] = useState<{value: string, id: string}[]>([])
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
                let entries: {value: string, id: string}[] = []
                response.data.forEach((u: GuildUser) => {
                    entries.push({
                        value: u.nickname,
                        id: u.id
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
        <Box sx={{marginY: "1vh"}}>
            <Autocomplete
                sx={{marginY: "1vh"}}
                multiple
                id="virtualize-demo"
                noOptionsText={"Start typing..."}
                disableListWrap
                PopperComponent={StyledPopper}
                ListboxComponent={ListboxComponent}
                options={roleOptions}
                getOptionLabel={(option) =>
                    typeof option === 'string' ? option : option.value
                }
                isOptionEqualToValue={(option, value) => {
                    return option.value === value.value
                }}
                renderInput={(params) => {
                    return <TextField {...params} label="Roles" />}}
                renderOption={(props, option) => [props, option.value] as React.ReactNode}
                // TODO: Post React 18 update - validate this conversion, look like a hidden bug
                renderGroup={(params) => params as unknown as React.ReactNode}
                onChange={(event, value, reason) => {
                    setSelectedRoles(value)
                }}
            />
            <Autocomplete
                sx={{marginY: "1vh"}}
                multiple
                id="virtualize-demo"
                noOptionsText={"Start typing..."}
                disableListWrap
                PopperComponent={StyledPopper}
                ListboxComponent={ListboxComponent}
                filterOptions={(x => x)}
                options={memberOptions}
                getOptionLabel={(option) =>
                    typeof option === 'string' ? option : option.value
                }
                isOptionEqualToValue={(option, value) => {
                    return option.value === value.value
                }}
                renderInput={(params) => <TextField {...params} label="Members" />}
                renderOption={(props, option) => [props, option.value] as React.ReactNode}
                // TODO: Post React 18 update - validate this conversion, look like a hidden bug
                renderGroup={(params) => params as unknown as React.ReactNode}
                onInputChange={(event, newInputValue) => {
                    setSearch(newInputValue);
                }}
                onChange={(event, value, reason) => {
                    setSelectedMembers(value)
                }}
            />
            <Box sx={{display: "flex", justifyContent: "right"}}>
                <Button variant={"contained"} onClick={clickButton}>Submit</Button>
            </Box>
        </Box>
    );
}
