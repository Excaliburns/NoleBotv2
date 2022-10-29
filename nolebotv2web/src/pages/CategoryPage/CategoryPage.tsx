import {Autocomplete, Box, TextField} from "@mui/material";
import getListbox from "../../shared/components/VirtualizedListbox";
import React, {useState} from "react";
import {useAxios} from "../../util/AxiosProvider";
import {Category} from "../../entities/JavaGenerated";

export default function CategoryPage() {
    const [categories, setCategories] = useState<{id: string, categoryName: string}[]>([])
    const [selectedCategory, setSelectedCategory] = useState<{id: string, categoryName: string}>()
    const [availableRoles, setAvailableRoles] = useState<{id: string, logoUrl?: string, name: string}[]>([])
    const [selectedRoles, setSelectedRoles] = useState<{id: string, logoUrl?: string, name: string}[]>([])
    const [owners, setOwners] = useState<{id: string, name: string, logoUrl?: string}[]>([])
    const [selectedOwners, setSelectedOwners] = useState<{id: string, name?: string, logoUrl?: string}[]>([])
    const axios = useAxios()

    React.useEffect(() => {
        axios.get("/category/" + process.env.REACT_APP_MAIN_SERVER_ID +"/list").then((response) => {
            let newCategories: {id: string, categoryName: string}[] = []
            let newSelectedRoles: {id: string, logoUrl?: string}[] = []
            let newSelectedOwners: {id: string, name?: string, logoUrl?: string}[] = []
            response.data.forEach((category: Category) => {
                newCategories.push({
                    id: category.id,
                    categoryName: category.categoryName
                })
                setCategories(newCategories)

            })
            console.log(categories)
        })
    }, [])

    return <Box>
        <Autocomplete
        renderInput={
            (params) => {
                return <TextField {...params} label={"Role Categories"}/>
            }
        }
        options={categories}
        getOptionLabel={(option) => {
            return option.categoryName
        }}
        ListboxComponent={getListbox()}
        onChange={(event, value, reason) => {
            setSelectedCategory(value)
        }}/>
        <Autocomplete
            multiple={true}
            renderInput={
                (params) => {
                    return <TextField {...params} label={"Roles"}/>
                }
            }
            options={availableRoles}
            value={selectedRoles}
            getOptionLabel={(option) => {
                return option.id
            }}
            ListboxComponent={getListbox()}
            onChange={(event, value, reason) => {
                setSelectedRoles(value)
            }}/>
    </Box>

}