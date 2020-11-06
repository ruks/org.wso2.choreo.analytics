import React from "react";
import { render } from "react-dom";
import {
    VictoryBar,
    VictoryTheme,
    VictoryChart,
    VictoryGroup,
    VictoryStack,
    VictoryAxis,
    VictoryClipContainer,
    VictoryTooltip
} from "victory";

class BarChart extends React.Component {
    render() {
        const { data } = this.props;
        console.log(data);
        const barRatio = 0.2;
        const barWidth = 6;
        const colorScale = ['tomato', 'orange', 'gold', 'green', 'blue', 'red'];

        return (
            <VictoryChart
                theme={VictoryTheme.material}
                responsive={false}
                domainPadding={{ x: [20, 50], y: [20, 50] }}
                padding={{
                    top: 20, bottom: 50, right: 50, left: 50,
                }}
                theme={VictoryTheme.material}
                height={200}
                width={800}
            >
                <VictoryAxis
                    label="Latency Time"
                    style={{
                        axis: { stroke: "#756f6a" },
                        axisLabel: { fontSize: 8, padding: 30 },
                        grid: { stroke: () => 0 },
                        ticks: { stroke: "grey", size: 5 },
                        tickLabels: { fontSize: 6, padding: 5 }
                    }}
                />
                <VictoryAxis
                    label="APIs"
                    dependentAxis
                    style={{
                        axis: { stroke: "#756f6a" },
                        axisLabel: { fontSize: 8, padding: 30 },
                        grid: { stroke: ({ tick }) => tick > 0.5 ? "red" : "grey" },
                        ticks: { stroke: "grey", size: 5 },
                        tickLabels: { fontSize: 6, padding: 5 }
                    }}
                />
                <VictoryStack labels={""}>
                    <VictoryBar
                        style={{ data: { fill: colorScale[1], cursor: 'pointer' } }}
                        alignment='start'
                        barRatio={barRatio}
                        barWidth={barWidth}
                        x={d => d.apiName}
                        y={d => d.avgBackendLatency}
                        data={data.map(row => ({
                            ...row,
                            label: ['Backend', row.apiName, row.avgBackendLatency]
                        }))}
                        alignment='start'
                        labelComponent={<VictoryTooltip />}
                        groupComponent={<VictoryClipContainer clipId={0} />}
                    />
                    <VictoryBar
                        style={{ data: { fill: colorScale[2], cursor: 'pointer' } }}
                        alignment='start'
                        barRatio={barRatio}
                        barWidth={barWidth}
                        x={d => d.apiName}
                        y={d => d.avgSecurityLatency}
                        data={data.map(row => ({
                            ...row,
                            label: ['Security', row.apiName, row.avgSecurityLatency]
                        }))}
                        alignment='start'
                        labelComponent={<VictoryTooltip />}
                        groupComponent={<VictoryClipContainer clipId={0} />}
                    />
                    <VictoryBar
                        style={{ data: { fill: colorScale[3], cursor: 'pointer' } }}
                        alignment='start'
                        barRatio={barRatio}
                        barWidth={barWidth}
                        x={d => d.apiName}
                        y={d => d.avgServiceLatency}
                        data={data.map(row => ({
                            ...row,
                            label: ['Service', row.apiName, row.avgServiceLatency]
                        }))}
                        alignment='start'
                        labelComponent={<VictoryTooltip />}
                        groupComponent={<VictoryClipContainer clipId={0} />}
                    />
                </VictoryStack>
            </VictoryChart>
        );
    }
}

export default BarChart;