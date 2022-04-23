import {GroupBase} from "react-select/dist/declarations/src/types";
import { Virtuoso } from 'react-virtuoso'
import {MenuListProps} from "react-select";
import React from "react";

export const optimizeSelect = {
    components: {
        MenuList: OptimizedMenuList
    },
}

function OptimizedMenuList<
    Option,
    IsMulti extends boolean,
    Group extends GroupBase<Option>
>
(props: MenuListProps<Option, IsMulti, Group>) {
    const ref = React.useRef<HTMLDivElement>(null);

    const { children, maxHeight } = props
    if (!children || !Array.isArray(children)) return null

    const height = 60 // default list height

    return (
        <div ref = {ref}>
            <Virtuoso
                totalCount={children.length}
                style={{height: Math.min(maxHeight, height * children.length) + 'px'}}
                itemContent=
                    { (index) =>
                        <div className={"option-wrapper"}>
                            {children[index]}
                        </div>
                    }
            />
        </div>
    )
}