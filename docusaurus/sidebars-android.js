module.exports = {
  mySidebar: [
    {
      type: "category",
      label: "Setup",
      items: [
        {
          type: "autogenerated",
          dirName: "01-basics",
        },
      ],
    },
    {
      type: "category",
      label: "Tutorials",
      items: [
            {
              type: 'doc',
              id: 'tutorials/video-calling', // document ID
              label: 'Video Call Tutorial', // sidebar label
            },
             {
              type: 'doc',
              id: 'tutorials/audio-room', // document ID
              label: 'Audio Room Tutorial', // sidebar label
            },
             {
              type: 'doc',
              id: 'tutorials/livestream', // document ID
              label: 'Livestream Tutorial', // sidebar label
            },
      ],
    },
    {
      type: "category",
      label: "Core Concepts",
      items: [
        {
          type: "autogenerated",
          dirName: "03-guides",
        },
      ],
    },
    {
      type: "category",
      label: "UI Components",
      items: [
        {
          type: "autogenerated",
          dirName: "04-ui-components",
        },
      ],
    },

    {
      type: "category",
      label: "UI Cookbook",
      items: [
        {
          type: "autogenerated",
          dirName: "05-ui-cookbook",
        },
      ],
    },
    {
      type: "category",
      label: "Advanced Guides",
      items: [
        {
          type: "autogenerated",
          dirName: "06-advanced",
        },
      ],
    },
  ],
};
