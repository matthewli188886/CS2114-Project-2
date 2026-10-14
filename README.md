# Hotel Simulator

CS2114 Project 2

A Java simulation of how a hotel operates over a long period of time. Instead of a
typical hotel management system where users manually add and manage reservations,
the program runs the hotel automatically. The user sets up the hotel and some basic
expectations, and the program generates different situations for each day.

The main goal is to see how well the hotel handles different levels of demand and
to find where problems or bottlenecks appear.

## Team

- Matthew Li
- Zichen Fu
- Dallan Barnum
- Jaidev Gogineni

## Reflection
- What changed from your specification? DailyData now stores simulationLength and totalRooms, which addDay needs for its checks. HotelSimulator gained a Scanner constructor along with getHotelData() and getDailyData(). The random ranges weren't specified in the doc so we chose guest count + or - 20% and stays from 1-7 days
- What changed in the LinkedChain? We simply used the default provided class, the only change was the package line at the top.
- What happened to your unknowns? We still don't have a graphical UI, a database or storage system, real time hotel reservations, online features, etc.
- As of now everything is pretty much functional, but could be polished or made more efficient


## Next Steps
- What is left to finish the MVP? The MVP is mostly done, what could be improved is the readability of the statistics so that the user can understand what the numbers actually mean.
- Which stretch goals will you attempt? We will attempt to have some sort of visual aspect to the project such as a graph, diagram, or GUI, so that its not just looking at the console.
- What new tests will you need? We will need to adjust the tests if we change the readability of the outputs right now, as well as creating new test classes if we implement the visual aspect.
- Who does what? Jaidev will handle the readability, Dallan will handle the new tests and bad inputs, Zichen and Matthew will handle the visual aspect


## Attribution
| Class & Tests  | Name | Did |
| ------------- | ------------- | ------------- |
| DailyData | Name |  |
| DailyRecord | Matthew  |  Drafted and wrote with GenAI Assistance  |
|  GuestData | Name |     |
|  GuestRecord | Matthew |  Drafted and wrote with GenAI Assistance   |
| GuestStatus | Name  |     |
| HotelData | Name  |     |
| HotelSimulator | Name  |     |
| Report | Name  |     |
| RoomData | Name  |     |
| RoomRecord | Matthew  |  Drafted and wrote with GenAI Assistance   |
| RoomStatus | Name  |     |
| Simulation | Name  |     |
