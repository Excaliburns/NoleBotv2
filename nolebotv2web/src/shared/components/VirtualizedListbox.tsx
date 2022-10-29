import React from "react";
import {Virtuoso} from "react-virtuoso";

export default function getListbox(): any {
    return React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLElement>>((props, ref) => {
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
}